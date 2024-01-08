package conf

public class ServerConf {
    companion object {
        @JvmStatic var mode: RunMode = RunMode.LOGLVL_RELEASE

        @JvmStatic fun process(arg: String) {
            when (arg) {
                "--loglvl-debug" -> mode = RunMode.LOGLVL_DEBUG
                "--loglvl-info" -> mode = RunMode.LOGLVL_INFO
            }
        }
    }
}