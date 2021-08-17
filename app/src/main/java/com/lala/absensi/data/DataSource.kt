package com.lala.absensi.data


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {

    companion object {
        val TAG = DataSource::class.java.simpleName
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun signIn(email: String, password: String): Flowable<Boolean> {
        val result = PublishSubject.create<Boolean>()
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    GlobalScope.launch {
                        if (auth.currentUser == null) {
                            result.onNext(false)
                        } else {
                            result.onNext(true)
                        }
                    }
                }
        } catch (e: Exception) {
            GlobalScope.launch {
                result.onNext(false)
            }
        }
        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun checkNisToEmailAndLogin(nis: String, password: String): Flowable<Boolean> {
        val result = PublishSubject.create<Boolean>()
        db.collection("murid")
            .addSnapshotListener { value, error ->
                val murid = value?.documents?.find {
                    it.get("nis") == nis
                }
                Log.d("CHECK", murid.toString())

                GlobalScope.launch {
                    val dbNis = murid?.get("nis")
                    val dbEmail = murid?.get("email").toString().lowercase()
                    Log.d("Check nisa", "$dbNis")
                    signIn(dbEmail, password).take(1).subscribe({
                        result.onNext(it)
                    })
                }
            }

        return result.toFlowable(BackpressureStrategy.BUFFER)
    }


    @ExperimentalCoroutinesApi
    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    fun checkAuth(): Flowable<String> {
        val result = PublishSubject.create<String>()
        if (auth.currentUser == null) {
            result.onNext("")
        } else {
            result.onNext(auth.uid.toString())
        }
        return result.toFlowable(BackpressureStrategy.BUFFER)
    }

}


//        val guru = Guru(
//            nuptk = "3556741642300033",
//            password = "12345678",
//            email = "Srimarfuah@gmail.com",
//            nama = "Sri Marfuah",
//            jenisKelamin = "P",
//        )
//
//        val murid = Murid(
//            nis = "0041111040",
//            email = "alfatih@gmail.com",
//            password = "12345678",
//            nama = "Alfatih",
//            jenisKelamin = "L",
//            jurusan = "RPL",
//            dataKehadiran = null
//        )
//
//        signUpWithEmail(murid)
//
//        signUpWithEmailGuru(guru)


//fun signUpWithEmail(murid: Murid) {
//
//    auth.createUserWithEmailAndPassword(murid.email.toString(), murid.password.toString())
//        .addOnCompleteListener {
//            val currentUser = auth.currentUser
//            val murid = Murid(
//                nis = murid.nis,
//                password = murid.password,
//                email = murid.email,
//                nama = murid.nama,
//                jenisKelamin = murid.jenisKelamin,
//                jurusan = murid.jurusan,
//                dataKehadiran = null
//            )
//
//            Log.d("SIGN UP", "${currentUser?.uid} $murid")
//            currentUser?.uid?.let {
//                db.collection("murid").document(it)
//                    .set(murid)
//                    .addOnSuccessListener {
//                        Log.w(MainActivity.TAG, "Berhasil db $murid")
//                    }
//                    .addOnFailureListener { e ->
//
//                        Log.w(MainActivity.TAG, "Error writing document", e)
//                    }
//            }
//        }
//}
//
//fun signUpWithEmailGuru(guru: Guru) {
//
//    auth.createUserWithEmailAndPassword(guru.email.toString(), guru.password.toString())
//        .addOnCompleteListener {
//            val currentUser = auth.currentUser
//            val guru = Guru(
//                nuptk = guru.nuptk,
//                password = guru.password,
//                email = guru.email,
//                nama = guru.nama,
//                jenisKelamin = guru.jenisKelamin,
//            )
//
//            Log.d("SIGN UP", "${currentUser?.uid} $guru")
//            currentUser?.uid?.let {
//                db.collection("guru").document(it)
//                    .set(guru)
//                    .addOnSuccessListener {
//                        Log.w(MainActivity.TAG, "Berhasil db $guru")
//                    }
//                    .addOnFailureListener { e ->
//
//                        Log.w(MainActivity.TAG, "Error writing document", e)
//                    }
//            }
//        }
//}