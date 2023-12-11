import antlr.cppLexer
import antlr.cppParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println("Incorrect args!")
        System.err.println("Usage: lab-3 <input file path> <output file path>")
        return
    }

    val inputFile = File(args[0])
    val outputFile = File(args[1])

    val lexer = cppLexer(CharStreams.fromReader(inputFile.bufferedReader()))
    val tokens = CommonTokenStream(lexer)
    val parser = cppParser(tokens)
    val walker = Walker()
    outputFile.writer().use {
        it.write(walker.visitCode(parser.code()))
    }
}
