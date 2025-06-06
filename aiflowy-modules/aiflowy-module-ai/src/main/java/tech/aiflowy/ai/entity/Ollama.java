package tech.aiflowy.ai.entity;


/**
 * Ollama类
 */
public class Ollama {
    private String name;
    private String id;
    private String size;

    public Ollama() {
    }

    public Ollama(String name, String id, String size) {
        this.name = name;
        this.id = id;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Ollama{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", size='" + size + '\'' +
                '}';
    }
}
