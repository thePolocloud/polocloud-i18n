package dev.httpmarco.polocloud.i18n.core.format

internal object PlaceholderFormatter {

    fun format(template: String, values: Map<String, Any?>): String {
        if (values.isEmpty() || !template.contains('{')) {
            return template
        }

        val result = StringBuilder(template.length + 32)

        var i = 0
        while (i < template.length) {

            val char = template[i]

            if (char == '{') {
                val end = template.indexOf('}', i)

                if (end > i + 1) {
                    val key = template.substring(i + 1, end)
                    val replacement = values[key]

                    if (replacement != null) {
                        result.append(replacement.toString())
                    } else {
                        result.append('{').append(key).append('}')
                    }

                    i = end + 1
                    continue
                }
            }

            result.append(char)
            i++
        }

        return result.toString()
    }
}