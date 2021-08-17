package com.lala.absensi

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lala.absensi.auth.AuthViewModel
import com.lala.absensi.ui.home.HomeGuru
import com.lala.absensi.ui.home.HomeMurid
import com.lala.absensi.ui.login.LoginGuru
import com.lala.absensi.ui.login.LoginMurid
import com.lala.absensi.ui.login.LoginViewModel
import com.lala.absensi.ui.splashscreen.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import javax.sql.DataSource

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    companion object {
        val TAG = DataSource::class.java.simpleName
    }

    @DelicateCoroutinesApi
    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setContent {
            Surface {
                //       Navigation()

//                if (auth.currentUser == null) {
//                    Log.d("Result", "null dongg")
//                } else {
//                    Log.d("Result",auth.uid.toString())
//                }
                val singin: Boolean? by signIn("ningrum@guru.com", "12345678").collectAsState(false)
                val value: String? by checkAuth().collectAsState("")
                Log.d("Login", singin.toString())
                GlobalScope.launch {
                    delay(3000)
                    Log.d("value after login ", value.toString())
                    delay(2000)
                    auth.signOut()
                    delay(1000)
                    Log.d("value after signOut ", value.toString())
                }

            }
        }
    }

    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    fun signIn(email: String, password: String): Flow<Boolean> = flow {
        val result = auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {}
        delay(2000)
        Log.d("RESULT",result.result.user?.email.toString() )
        if (result.result.user?.email == null) {
            emit(false)
        } else {
            emit(true)
        }

    }.flowOn(Dispatchers.IO)


    @ExperimentalCoroutinesApi
    @DelicateCoroutinesApi
    @ObsoleteCoroutinesApi
    fun checkAuth(): Flow<String> = flow {

        if (auth.currentUser == null) {
            emit("null dong")
        } else {
            emit(auth.uid.toString())
        }
    }.flowOn(Dispatchers.IO)

}

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(Screen.SplashScreen.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val value: String? by authViewModel.checkAuth().observeAsState()
            SplashScreen(navController = navController, auth = value.toString())
        }
        composable(Screen.LoginMuridScreen.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val nis: String by loginViewModel.text.observeAsState("")
            val password: String by loginViewModel.password.observeAsState("")
            val nisOnchange: (String) -> Unit = { loginViewModel.onChangeText(it) }
            val passwordOnchange: (String) -> Unit = { loginViewModel.onChangePassword(it) }
            val login: Boolean by loginViewModel.signInGuru(nis, password).observeAsState(false)

            LoginMurid(
                navController = navController,
                nis = nis,
                password = password,
                nisOnchange = nisOnchange,
                passwordOnchage = passwordOnchange,
                login = login
            )
        }
        composable(Screen.LoginGuruScreen.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val text: String by loginViewModel.text.observeAsState("")
            val password: String by loginViewModel.password.observeAsState("")
            val textOnchange: (String) -> Unit = {
                loginViewModel.onChangeText(it)
                Log.d("Text onchange", it)
            }
            val passwordOnchange: (String) -> Unit = {
                loginViewModel.onChangePassword(it)
                Log.d("Password onchange", it)
            }
            val login: Boolean by loginViewModel.signInGuru(text, password).observeAsState(false)

            LoginGuru(
                navController = navController,
                text = text,
                password = password,
                textOnchange = textOnchange,
                passwordOnchage = passwordOnchange,
                login = login
            )


        }
        composable(Screen.HomeMuridScreen.route) {
            HomeMurid()
        }
        composable(Screen.HomeGuruScreen.route) {
            HomeGuru()
        }
    }
}





