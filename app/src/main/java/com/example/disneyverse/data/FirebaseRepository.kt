package com.example.disneyverse.data

import com.example.disneyverse.model.Film
import com.example.disneyverse.model.Universe
import com.example.disneyverse.model.User
import com.example.disneyverse.model.UserFilmStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun currentUserId(): String? = auth.currentUser?.uid

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Login error") }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val user = User(uid = uid, username = username, email = email)

                db.child("users").child(uid).setValue(user)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError(it.message ?: "Database error") }
            }
            .addOnFailureListener { onError(it.message ?: "Register error") }
    }

    fun logout() {
        auth.signOut()
    }

    fun getUniverses(onResult: (List<Universe>) -> Unit) {
        db.child("universes").get().addOnSuccessListener { snapshot ->
            val universes = snapshot.children.mapNotNull { it.getValue(Universe::class.java) }
            onResult(universes)
        }
    }

    fun getFilmsByUniverse(universeId: String, onResult: (List<Film>) -> Unit) {
        db.child("films").get().addOnSuccessListener { snapshot ->
            val films = snapshot.children
                .mapNotNull { it.getValue(Film::class.java) }
                .filter { it.universeId == universeId }
                .sortedBy { it.releaseDate }

            onResult(films)
        }
    }

    fun getFilmById(filmId: String, onResult: (Film?) -> Unit) {
        db.child("films").child(filmId).get().addOnSuccessListener { snapshot ->
            onResult(snapshot.getValue(Film::class.java))
        }
    }

    fun getCurrentUserFilmStatus(filmId: String, onResult: (String?) -> Unit) {
        val uid = currentUserId() ?: return
        db.child("userFilms").child(uid).child(filmId).get().addOnSuccessListener { snapshot ->
            onResult(snapshot.getValue(UserFilmStatus::class.java)?.status)
        }
    }

    fun saveFilmStatus(
        filmId: String,
        status: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = currentUserId() ?: return
        val value = UserFilmStatus(filmId = filmId, status = status)

        db.child("userFilms").child(uid).child(filmId).setValue(value)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Status save error") }
    }

    fun removeFilmStatus(
        filmId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = currentUserId() ?: return
        db.child("userFilms").child(uid).child(filmId).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Remove status error") }
    }

    fun getOwnersAndGetRidUsers(
        filmId: String,
        onResult: (List<User>, List<User>) -> Unit
    ) {
        db.child("userFilms").get().addOnSuccessListener { statusSnapshot ->
            val ownerIds = mutableListOf<String>()
            val getRidIds = mutableListOf<String>()

            for (userNode in statusSnapshot.children) {
                val uid = userNode.key ?: continue
                val filmNode = userNode.child(filmId)
                val statusObj = filmNode.getValue(UserFilmStatus::class.java)

                if (statusObj != null) {
                    when (statusObj.status) {
                        "own" -> ownerIds.add(uid)
                        "want_to_get_rid" -> getRidIds.add(uid)
                    }
                }
            }

            db.child("users").get().addOnSuccessListener { usersSnapshot ->
                val owners = mutableListOf<User>()
                val getRidUsers = mutableListOf<User>()

                for (userNode in usersSnapshot.children) {
                    val user = userNode.getValue(User::class.java) ?: continue
                    if (ownerIds.contains(user.uid)) owners.add(user)
                    if (getRidIds.contains(user.uid)) getRidUsers.add(user)
                }

                onResult(owners, getRidUsers)
            }
        }
    }

    fun getMyStatusesAndOwnedFilms(
        onResult: (
            user: User?,
            watched: Int,
            wantToWatch: Int,
            own: Int,
            getRid: Int,
            ownedFilms: List<Film>
        ) -> Unit
    ) {
        val uid = currentUserId() ?: return

        db.child("users").child(uid).get().addOnSuccessListener { userSnapshot ->
            val user = userSnapshot.getValue(User::class.java)

            db.child("userFilms").child(uid).get().addOnSuccessListener { statusSnapshot ->
                var watched = 0
                var wantToWatch = 0
                var own = 0
                var getRid = 0
                val ownedIds = mutableListOf<String>()

                for (child in statusSnapshot.children) {
                    val statusObj = child.getValue(UserFilmStatus::class.java) ?: continue

                    when (statusObj.status) {
                        "watched" -> watched++
                        "want_to_watch" -> wantToWatch++
                        "own" -> {
                            own++
                            ownedIds.add(statusObj.filmId)
                        }
                        "want_to_get_rid" -> getRid++
                    }
                }

                db.child("films").get().addOnSuccessListener { filmsSnapshot ->
                    val ownedFilms = filmsSnapshot.children
                        .mapNotNull { it.getValue(Film::class.java) }
                        .filter { ownedIds.contains(it.id) }
                        .sortedBy { it.title }

                    onResult(user, watched, wantToWatch, own, getRid, ownedFilms)
                }
            }
        }
    }
}