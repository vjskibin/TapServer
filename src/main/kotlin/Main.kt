import conf.ServerConf

fun main(args: Array<String>) {
    for (arg in args) ServerConf.process(arg)
    Server().run()
}