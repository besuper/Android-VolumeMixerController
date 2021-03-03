package be.besuper.volumemixercontroller.computer;

public class Computer {

    private String name;
    private final String bind;

    public Computer(final String name, final String bind){
        this.name = name;
        this.bind = bind;
    }

    public String getName() {
        return name;
    }

    public String getBind() {
        return bind;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
