package me.ssttkkl.mrmemorizer.data.entity

import java.util.*

class Tree(
    val content: String,
    val children: Collection<Tree>
) {
    private fun appendDisplayText(sb: StringBuilder, prefixNum: Int) {
        for (i in 0 until prefixNum) sb.append(' ')
        sb.append(content)
        sb.append('\n')
        for (c in children) {
            c.appendDisplayText(sb, prefixNum + 1)
        }
    }

    fun toDisplayText(): String {
        val sb = StringBuilder()
        appendDisplayText(sb, 0)
        sb.deleteCharAt(sb.length - 1) // 删除最后一行的换行
        return sb.toString()
    }

    companion object {
        @JvmStatic
        fun parseText(text: String): Tree {
            val lines = text.split("\n").toMutableList()
            val root = lines[0]
            val prevChildText = StringBuilder()
            val children = ArrayList<Tree>()
            for (i in 1 until lines.size) {
                if (lines[i].isEmpty())
                    continue
                lines[i] = lines[i].substring(1)
                if (!lines[i].startsWith(" ")) {
                    if (prevChildText.isNotEmpty()) {
                        children.add(parseText(prevChildText.toString()))
                    }
                    prevChildText.delete(0, prevChildText.length)
                } else {
                    prevChildText.append('\n')
                }
                prevChildText.append(lines[i])
            }
            if (prevChildText.isNotEmpty()) {
                children.add(parseText(prevChildText.toString()))
            }
            return Tree(root, children)
        }
    }

}