package com.learn.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.learn.annotation.GenerateGreeting
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec

class GreetingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Find symbols annotated with @GenerateGreeting
        val symbols = resolver.getSymbolsWithAnnotation(GenerateGreeting::class.qualifiedName!!)

        symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
            // Generate code for each annotated class
            generateGreetingClass(classDeclaration)
        }

        return emptyList()
    }

    private fun generateGreetingClass(classDeclaration: KSClassDeclaration) {
        val fileName = "${classDeclaration.simpleName.asString()}Generated"
        val packageName = classDeclaration.packageName.asString()

        // Create the function to be generated
        val getGreetingsFunction = FunSpec.builder("getGreetings")
            .addStatement("println(%S)", "Hello, World!")
            .build()

        // Create the class with the function
        val generatedClass = TypeSpec.classBuilder(fileName)
            .addFunction(getGreetingsFunction)
            .build()

        // Generate the Kotlin file
        val fileSpec = FileSpec.builder(packageName, fileName)
            .addType(generatedClass)
            .build()

        // Write the file using the CodeGenerator
        codeGenerator.createNewFile(
            Dependencies(false),
            packageName,
            fileName
        ).writer().use { writer ->
            fileSpec.writeTo(writer)
        }
    }
}
