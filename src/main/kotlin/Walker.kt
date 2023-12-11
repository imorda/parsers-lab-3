import antlr.cppBaseVisitor
import antlr.cppParser

class Walker() : cppBaseVisitor<String>() {
    var curIndent = 0
    var isLastVarDeclaration = false

    override fun visitCode(ctx: cppParser.CodeContext?): String {
        return super.visitCode(ctx).trimEnd() + newLine()
    }

    override fun visitStatement(ctx: cppParser.StatementContext?): String {
        return when {
            ctx!!.variableDeclaration() != null -> {
                isLastVarDeclaration = true
                visitVariableDeclaration(ctx.variableDeclaration()) + ";"
            }
            ctx.variableAssignment() != null -> {
                val isCurVarDeclaration = isLastVarDeclaration
                isLastVarDeclaration = false
                (if (isCurVarDeclaration) newLine() else "") + visitVariableAssignment(ctx.variableAssignment()) + ";"
            }
            else -> {
                val isCurVarDeclaration = isLastVarDeclaration
                isLastVarDeclaration = false
                (if (isCurVarDeclaration) newLine() else "") + super.visitStatement(ctx)
            }
        } + newLine()
    }

    override fun visitFunctionDeclaration(ctx: cppParser.FunctionDeclarationContext?): String {
        val builder = StringBuilder()
        val vars = ctx!!.variableDeclaration()

        if (vars.isNotEmpty()) {
            builder.append(visitVariableDeclaration(vars[0]))
            for (i in 1..<vars.size) {
                builder.append(", ")
                builder.append(vars[i])
            }
        }

        return ctx.NAME(0).text + " " + ctx.NAME(1).text + "(" + builder.toString() + ") " +
            visitFunctionBody(ctx.functionBody())
    }

    override fun visitFunctionBody(ctx: cppParser.FunctionBodyContext?): String {
        val builder = StringBuilder()

        builder.append("{")
        curIndent++
        builder.append(newLine())

        for (i in 0..<ctx!!.childCount) {
            val child = ctx.getChild(i)
            when {
                child is cppParser.ExpressionContext -> {
                    if (isLastVarDeclaration) builder.append(newLine())
                    builder.append(visitExpression(child) + ";" + newLine())
                    isLastVarDeclaration = false
                }
                child is cppParser.StatementContext -> builder.append(visitStatement(child))
                child is cppParser.ReturnContext -> {
                    if (isLastVarDeclaration) builder.append(newLine())
                    builder.append(visitReturn(child, if (isLastVarDeclaration) System.lineSeparator() else "") + newLine())
                    isLastVarDeclaration = false
                }
                else -> continue
            }
        }

        for (i in 0..<4) {
            builder.deleteCharAt(builder.length - 1)
        }
        builder.append("}")
        curIndent--
        builder.append(newLine())

        return builder.toString()
    }

    override fun visitReturn(ctx: cppParser.ReturnContext?): String {
        return visitReturn(ctx, "")
    }

    private fun visitReturn(ctx: cppParser.ReturnContext?, lastSymbol: String): String {
        return (if (lastSymbol == System.lineSeparator()) { "" } else newLine()) + "return " + super.visitReturn(ctx) + ";"
    }

    override fun visitDirective(ctx: cppParser.DirectiveContext?): String {
        return "#include " + ctx!!.STRING().text + newLine()
    }

    override fun visitVariableDeclaration(ctx: cppParser.VariableDeclarationContext?): String {
        return ctx!!.NAME().text + " " + super.visitVariableDeclaration(ctx)
    }

    override fun visitVariableAssignment(ctx: cppParser.VariableAssignmentContext?): String {
        return ctx!!.NAME().text + ((ctx.expression()?.let { " = " + visitExpression(it) }) ?: "")
    }

    override fun visitExpression(ctx: cppParser.ExpressionContext?): String {
        return when {
            ctx!!.NOT() != null -> "!" + visitExpression(ctx.expression(0))
            ctx.MULTIPLY() != null -> visitExpression(ctx.expression(0)) + " * " + visitExpression(ctx.expression(1))
            ctx.DIVIDE() != null -> visitExpression(ctx.expression(0)) + " / " + visitExpression(ctx.expression(1))
            ctx.PLUS() != null -> visitExpression(ctx.expression(0)) + " + " + visitExpression(ctx.expression(1))
            ctx.MINUS() != null -> visitExpression(ctx.expression(0)) + " - " + visitExpression(ctx.expression(1))
            ctx.LE() != null -> visitExpression(ctx.expression(0)) + " <= " + visitExpression(ctx.expression(1))
            ctx.LANGLE() != null -> visitExpression(ctx.expression(0)) + " < " + visitExpression(ctx.expression(1))
            ctx.GE() != null -> visitExpression(ctx.expression(0)) + " >= " + visitExpression(ctx.expression(1))
            ctx.RANGLE() != null -> visitExpression(ctx.expression(0)) + " > " + visitExpression(ctx.expression(1))
            ctx.EQ() != null -> visitExpression(ctx.expression(0)) + " == " + visitExpression(ctx.expression(1))
            ctx.NE() != null -> visitExpression(ctx.expression(0)) + " != " + visitExpression(ctx.expression(1))
            ctx.AND() != null -> visitExpression(ctx.expression(0)) + " && " + visitExpression(ctx.expression(1))
            ctx.OR() != null -> visitExpression(ctx.expression(0)) + " || " + visitExpression(ctx.expression(1))
            ctx.term() != null -> visitTerm(ctx.term())
            else -> visitFunctionInvocation(ctx.functionInvocation())
        }
    }

    override fun visitTerm(ctx: cppParser.TermContext?): String {
        return when {
            ctx!!.NUMBER() != null -> ctx.NUMBER().text
            ctx.STRING() != null -> ctx.STRING().text
            ctx.NAME() != null -> ctx.NAME().text
            else -> "(" + visitExpression(ctx.expression()) + ")"
        }
    }

    override fun visitFunctionInvocation(ctx: cppParser.FunctionInvocationContext?): String {
        val builder = StringBuilder()
        val expressions = ctx!!.expression()
        builder.append(visitExpression(expressions[0]))
        for (i in 1..<expressions.size) {
            builder.append(", ")
            builder.append(visitExpression(expressions[i]))
        }
        return ctx.NAME().text + "(" + builder.toString() + ")"
    }

    override fun aggregateResult(aggregate: String?, nextResult: String?): String {
        return (aggregate ?: "") + (nextResult ?: "")
    }

    private fun newLine(): String {
        return System.lineSeparator() + " ".repeat(curIndent * 4)
    }
}
