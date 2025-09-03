package pt.isec.a2022141906.tp1.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.launch
import pt.isec.a2022141906.tp1.R
import pt.isec.a2022141906.tp1.models.User
import pt.isec.a2022141906.tp1.utils.FAuthUtil

fun FirebaseUser.toUser(): User {
    val displayName = this.displayName ?: ""
    val strEmail = this.email ?: ""
    val picture = this.photoUrl?.toString()
    val uid = this.uid // Obtém o ID único do Firebase Authentication

    return User(displayName, strEmail, picture, uid)
}

class UserViewModel : ViewModel() {
    // Estado do utilizador
    private val _user = mutableStateOf(FAuthUtil.currentUser?.toUser())
    val user: State<User?>
        get() = _user

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?>
        get() = _error

    // Criação de conta
    fun createUserWithEmail(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        photoUrl: String?,
        context: Context
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            Toast.makeText(context, context.getString(R.string.fill_mandatory_fields), Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            if (password == confirmPassword) {
                FAuthUtil.createUserWithEmailAndProfile(email, password, name, photoUrl, context) { exception ->
                    if (exception == null) {
                        _user.value = FAuthUtil.currentUser?.toUser()
                    }
                    _error.value = exception?.message
                }
            } else {
                _error.value = context.getString(R.string.passwords_dont_match)
            }
        }
    }

    // Login
    fun signInWithEmail(email: String, password: String, context: Context) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, context.getString(R.string.fill_mandatory_fields), Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            FAuthUtil.signInWithEmail(email, password) { exception ->
                if (exception == null) {
                    _user.value = FAuthUtil.currentUser?.toUser()
                }
                _error.value = exception?.message
            }
        }
    }

    // Logout
    fun signOut() {
        FAuthUtil.signOut()
        _user.value = null
        _error.value = null
    }

    fun updateUserName(name: String, context: Context) {
        val currentUser = FAuthUtil.currentUser
        if (currentUser != null) {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Atualizar o estado local do utilizador
                        _user.value = currentUser.toUser()
                    } else {
                        Toast.makeText(context, "Failed to update name: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "No user is currently signed in", Toast.LENGTH_SHORT).show()
        }
    }

}