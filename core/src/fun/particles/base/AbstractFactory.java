package fun.particles.base;

public interface AbstractFactory<V> {
    <T extends V> T create(Class<T> name);
}

