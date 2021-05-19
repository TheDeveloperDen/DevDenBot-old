package me.bristermitten.devdenbot.pasting

import kotlin.test.Test
import kotlin.test.assertEquals

internal class CodeBlockMessageListenerTest {

    private val multipleCodeBlocks = """
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
    """.trimIndent()

    private val multipleCodeBlocksFirstCodeBlock = """
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

    private val multipleCodeBlocksSecondCodeBlock = """
        public static void main(String[] args) {
                Scanner Msg = new Scanner(System.in);
                int x = 1;
                System.out.prinln();
                System.out.prinln();
                System.out.prinln();
        }
        """.trimIndent()

    private val multipleCodeBlocksOneTooSmallSecondCodeBlock = """
        this is
        once again
        a smol
        code block
    """.trimIndent()

    private val codeBlockWithLanguage = """
        ```somelang
        this is
        once again
        a smol
        code block
        ```
    """.trimIndent()

    private val codeBlockWithoutNewLines = """
        ```this is
        once again
        a smol
        code block```
    """.trimIndent()

    @Test
    fun `regex matches multiple code blocks`() {
        val matches = CodeBlockMessageListener.codeBlock.findAll(multipleCodeBlocks)
        assertEquals(2, matches.count())
        val firstMatchGroups = matches.elementAt(0).groups.filterNotNull()
        // first group is the whole match, 2nd group the language if given and 3rd group is the code block
        assertEquals(3, firstMatchGroups.size)
        assertEquals(multipleCodeBlocksFirstCodeBlock, firstMatchGroups.last()!!.value.trimEnd())
        assertEquals("java", firstMatchGroups[1]!!.value)

        val secondMatchGroups = matches.elementAt(1).groups.filterNotNull()
        assertEquals(2,  secondMatchGroups.size)
        assertEquals(multipleCodeBlocksSecondCodeBlock, secondMatchGroups.last().value.trimEnd())
    }

    @Test
    fun `regex matches code block with language`() {
        val matches = CodeBlockMessageListener.codeBlock.findAll(codeBlockWithLanguage)
        assertEquals(1, matches.count())
        val firstMatchGroups = matches.elementAt(0).groups.filterNotNull()
        assertEquals(3, firstMatchGroups.size)
        assertEquals(multipleCodeBlocksOneTooSmallSecondCodeBlock, firstMatchGroups.last()!!.value.trimEnd())
        assertEquals("somelang", firstMatchGroups[1]!!.value)

    }

    @Test
    fun `regex matches code block without newlines`() {
        val matches = CodeBlockMessageListener.codeBlock.findAll(codeBlockWithoutNewLines)
        assertEquals(1, matches.count())
        val firstMatchGroups = matches.elementAt(0).groups.filterNotNull()
        assertEquals(2, firstMatchGroups.size)
        assertEquals(multipleCodeBlocksOneTooSmallSecondCodeBlock, firstMatchGroups.last()!!.value.trimEnd())

    }
}