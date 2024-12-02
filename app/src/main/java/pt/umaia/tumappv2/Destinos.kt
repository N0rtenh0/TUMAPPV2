package pt.umaia.tumappv2

sealed class Destino(val route: String, val icon: Int, val title: String) {
    object EcraLoginFirebase : Destino(route = "EcraLoginFirebase", icon = R.drawable.login, title = "Login")
    object EcraRegisterToFirebase : Destino(route = "EcraRegisterToFirebase", icon = R.drawable.register, title = "Register")
    object EcraSettings : Destino(route = "EcraSettings", icon = R.drawable.edita, title = "Settings")
    object Ecra03 : Destino(route = "ecra03", icon = R.drawable.lista, title = "Lista")
    object Ecra04 : Destino(route = "ecra04", icon = R.drawable.lista_add, title = "Editar")
    companion object {
        val toList = listOf(Ecra03, Ecra04, EcraSettings) // a ordem define a ordem dos ecrãs e só os ecrãs listados aqui aparecem em baixo
    }
}