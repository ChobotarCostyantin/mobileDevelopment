sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Login : Routes("login")
    object Register : Routes("register")
    object Profile : Routes("profile")
    object Quiz : Routes("quiz")
    object Articles : Routes("articles")
    object Settings : Routes("settings")
}