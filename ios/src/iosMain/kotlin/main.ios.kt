import androidx.compose.ui.window.Application
import com.lt.lazy_people_http.common.App
import platform.UIKit.UIViewController

fun ComposeViewController(): UIViewController =
    Application("ComposeView") {
        App()
    }
