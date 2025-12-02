package maestro.orchestra.yaml

/**
 * Represents parametrized data for flow execution.
 * 
 * Data is defined as a map where:
 * - Key: variable name used in templates (e.g., ${productName})
 * - Value: list of values to iterate through
 * 
 * Example YAML:
 * ```
 * data:
 *   productName: ["Phone", "Laptop", "Shirt"]
 *   category: ["Electronics", "Electronics", "Apparel"]
 * ```
 * 
 * The flow will be executed 3 times (for each set of values).
 */
data class YamlDataParametrization(
    val data: Map<String, List<String>>
) {
    /**
     * Returns the number of iterations based on the longest list.
     * All lists should have the same length, but this handles edge cases.
     */
    fun getIterationCount(): Int {
        return data.values.maxOfOrNull { it.size } ?: 0
    }

    /**
     * Generates a map of variables for a specific iteration index.
     * Returns null if index is out of bounds.
     */
    fun getDataSetForIteration(iterationIndex: Int): Map<String, String>? {
        if (iterationIndex >= getIterationCount()) {
            return null
        }

        return data.mapValues { (_, values) ->
            // Get value at index, or empty string if list is shorter
            if (iterationIndex < values.size) values[iterationIndex] else ""
        }
    }

    /**
     * Validates that all data lists have the same length.
     * Returns true if valid, false otherwise.
     */
    fun isValid(): Boolean {
        if (data.isEmpty()) return true
        val sizes = data.values.map { it.size }.distinct()
        return sizes.size <= 1
    }

    /**
     * Validates that all data lists have the same length and throws an exception if not.
     */
    fun validateOrThrow() {
        if (!isValid()) {
            val sizes = data.entries.groupBy { it.value.size }
                .mapKeys { "size ${it.key}" }
                .mapValues { it.value.map { e -> e.key } }
            throw IllegalArgumentException(
                "All data lists must have the same length. Found: $sizes"
            )
        }
    }
}
