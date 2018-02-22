package org.cyclops.commoncapabilities.api.ingredient;

/**
 * An instance matcher for certain instance and condition types.
 * @param <T> The instance type to match.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public interface IIngredientMatcher<T, M> {

    /**
     * Check if the two given instances match based on the given match conditions.
     * @param a A first instance.
     * @param b A second instance.
     * @param matchCondition A condition under which the matching should be done.
     * @return If the two given instances match under the given conditions.
     */
    public boolean matches(T a, T b, M matchCondition);

    /**
     * Check if the two given instances are equal.
     * @param a A first instance.
     * @param b A second instance.
     * @return If the two given instances are equal.
     */
    public boolean matchesExactly(T a, T b);

    /**
     * Hash the given instance.
     * This must be calculated quickly.
     * @param instance An instance.
     * @return A hashcode for the given instance.
     */
    public int hash(T instance);

}
