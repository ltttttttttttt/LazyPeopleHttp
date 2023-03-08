import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lt.lazy_people_http.common.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
