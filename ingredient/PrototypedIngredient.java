package org.cyclops.commoncapabilities.api.ingredient;

import java.util.Objects;

/**
 * A raw prototyped ingredient.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public class PrototypedIngredient<T, M> implements IPrototypedIngredient<T, M> {

    private final IngredientComponent<T, M> ingredientComponent;
    private final T prototype;
    private final M condition;

    public PrototypedIngredient(IngredientComponent<T, M> ingredientComponent, T prototype, M condition) {
        this.ingredientComponent = ingredientComponent;
        this.prototype = prototype;
        this.condition = condition;
    }

    @Override
    public IngredientComponent<T, M> getComponent() {
        return ingredientComponent;
    }

    @Override
    public T getPrototype() {
        return prototype;
    }

    @Override
    public M getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IPrototypedIngredient) {
            IPrototypedIngredient<?, ?> that = (IPrototypedIngredient<?, ?>) obj;
            return this.getComponent().equals(that.getComponent())
                    && this.getComponent().getMatcher().matchesExactly(this.getPrototype(), (T) that.getPrototype())
                    && Objects.equals(this.getCondition(), that.getCondition());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 394 | this.getComponent().hashCode() << 4
                | this.getComponent().getMatcher().hash(this.getPrototype()) << 2
                | this.getCondition().hashCode();
    }

    @Override
    public String toString() {
        return "[PrototypedIngredient ingredientComponent: " + ingredientComponent.toString()
                + "; prototype: " + prototype.toString()
                + "; condition: " + condition.toString()
                + "]";
    }
}
