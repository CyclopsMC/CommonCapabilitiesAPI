package org.cyclops.commoncapabilities.api.ingredient;

/**
 * An ingredient that is identified by a given instance and can be matched with other instances under a given condition.
 *
 * Implementing classes should properly implement the equals and hashCode methods.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public interface IPrototypedIngredient<T, M> extends Comparable<IPrototypedIngredient<?, ?>> {

    /**
     * @return The type of ingredient component this prototype exists for.
     */
    public IngredientComponent<T, M> getComponent();

    /**
     * @return The prototype of this ingredient.
     */
    public T getPrototype();

    /**
     * @return The condition under which the prototype instance can be matched.
     */
    public M getCondition();

}
