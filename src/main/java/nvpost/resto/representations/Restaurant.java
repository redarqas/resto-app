package nvpost.resto.representations;

import org.hibernate.validator.constraints.NotBlank;

public class Restaurant implements Comparable<Restaurant>{
    @NotBlank
    private String name;

    public Restaurant(String name) {
        this.name = name;
    }

    public Restaurant() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final Restaurant resto = (Restaurant) obj;
        return (name.equals(resto.name));
    }

    @Override
    public int hashCode() {
        return 41 * (41 + name.hashCode());
    }
    
    @Override
    public String toString() {
        return String.format("Restaurant(%1$s)", name);
    }

    public int compareTo(Restaurant o) {
        return this.name.compareTo(o.name);
    }
}
