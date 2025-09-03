package pt.isec.a2022141906.tp1.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import pt.isec.a2022141906.tp1.R

class FAuthUtil {
    companion object {
        private val auth by lazy { Firebase.auth }

        val currentUser: FirebaseUser?
            get() = auth.currentUser

        fun createUserWithEmailAndProfile(
            email: String,
            password: String,
            name: String,
            photoUrl: String?,
            context: Context,
            onResult: (Throwable?) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            // Atualizar o perfil do usuário
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                                photoUri = photoUrl?.let { Uri.parse(it) }
                            }

                            user.updateProfile(profileUpdates)
                                .addOnCompleteListener { profileUpdateResult ->
                                    if (profileUpdateResult.isSuccessful) {
                                        // Tudo configurado corretamente
                                        onResult(null)
                                    } else {
                                        // Erro ao atualizar o perfil
                                        onResult(profileUpdateResult.exception)
                                    }
                                }
                        } else {
                            onResult(Exception(context.getString(R.string.user_not_found)))
                        }
                    } else {
                        // Erro ao criar a conta
                        val exception = result.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            // Email já em uso
                            onResult(Exception(context.getString(R.string.email_already_used)))
                        } else {
                            // Outro erro
                            onResult(result.exception)
                        }
                    }
                }
        }

        fun signInWithEmail(email: String, password: String, onResult: (Throwable?) -> Unit) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    onResult(result.exception)
                }
        }

        fun signOut() {
            if (auth.currentUser != null) {
                auth.signOut()
            }
        }
    }
}