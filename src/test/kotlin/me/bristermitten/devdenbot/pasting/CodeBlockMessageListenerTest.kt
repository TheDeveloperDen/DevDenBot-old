package me.bristermitten.devdenbot.pasting

import kotlin.test.Test
import kotlin.test.assertEquals

internal class CodeBlockMessageListenerTest {

    val multipleCodeBlocks = """
        ```java
        import java.util.Scanner;
        public class MessageTooLongException {

            static void Exception() {
                String MsgTooLong = getMessage("This Message is too long!");
                System.out.println(MsgTooLong);
            }

            public static String getMessage(String m) {
                return m;
            }
        ```
        this was a text
        ```public static void main(String[] args) {
                Scanner Msg = new Scanner(System.in);
                int x = 1;
                System.out.prinln();
                System.out.prinln();
                System.out.prinln();
        }```
        Hey guys this is some work i had, this i what i've done so far but it needs to use try/catch and im not sure how to make it so.
        ```
        fun main() = println(""${'"'}
        // some small code i made""${'"'}
        ```
    """.trimIndent()

    val multipleCodeBlocksFirstCodeBlock = """
        import java.util.Scanner;
        public class MessageTooLongException {

            static void Exception() {
                String MsgTooLong = getMessage("This Message is too long!");
                System.out.println(MsgTooLong);
            }

            public static String getMessage(String m) {
                return m;
            }
    """.trimIndent()

    val multipleCodeBlocksSecondCodeBlock = """
        public static void main(String[] args) {
                Scanner Msg = new Scanner(System.in);
                int x = 1;
                System.out.prinln();
                System.out.prinln();
                System.out.prinln();
        }
        """.trimIndent()

    val smallCodeBlock = """
         Hey guys this is some work i had, this i what i've done so far but it needs to use try/catch and im not sure how to make it so.
        ```
        fun main() = println(""${'"'}
        // some small code i made""${'"'}
        ```
    """.trimIndent()

    @Test
    fun `regex matches multiple code blocks`() {
        val matches = CodeBlockMessageListener.largeCodeBlock.findAll(multipleCodeBlocks)
        assertEquals(2, matches.count())
        val firstMatch = matches.elementAt(0)
        assertEquals(2, firstMatch.groups.size) // one group is the match, the other is the code block
        assertEquals(multipleCodeBlocksFirstCodeBlock, firstMatch.groups[1]!!.value.trimEnd()) // newLines at the end are fine

        val secondMatch = matches.elementAt(1)
        assertEquals(2, secondMatch.groups.size)
        assertEquals(multipleCodeBlocksSecondCodeBlock, secondMatch.groups[1]!!.value.trimEnd())
    }

    @Test
    fun `regex does not match small code block`() {
        val matches = CodeBlockMessageListener.largeCodeBlock.findAll(smallCodeBlock)
        assertEquals(0, matches.count())
    }
}