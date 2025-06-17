package sm_smc.ci

class Messages {
    static void sayHello(script) {
        script.echo "Hello from Test class via shared lib"
    }
    static void checkOut(script) {
        script.echo "Checking out"
    }
}
