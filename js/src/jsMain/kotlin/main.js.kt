import androidx.compose.ui.window.Window
import com.lt.lazy_people_http.common.App
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    onWasmReady {
        Window("LazyPeopleHttp") {
            App()
        }
    }
}

