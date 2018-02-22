package org.cyclops.commoncapabilities.api.ingredient;

import java.util.Objects;

/**
 * A raw prototyped ingredient.
 *
 * @param <T> The instance type.
 * @param <R> The recipe target type, may be Void.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public class PrototypedIngredient<T, R, M> implements IPrototypedIngredient<T, R, M> {

    private final IngredientComponent<T, R, M> ingredientComponent;
    private final T prototype;
    private final M condition;

    public PrototypedIngredient(IngredientComponent<T, R, M> ingredientComponent, T prototype, M condition) {
        this.ingredientComponent = ingredientComponent;
        this.prototype = prototype;
        this.condition = condition;
    }

    @Override
    public IngredientComponent<T, R, M> getComponent() {
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
            IPrototypedIngredient<?, ?, ?> that = (IPrototypedIngredient<?, ?, ?>) obj;
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
