package io.github.mattshoe.shoebox.autorepo.processors.impl

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import io.github.mattshoe.shoebox.autorepo.model.GeneratedFileData
import kotlin.math.log

class NoCacheProcessor(
    logger: KSPLogger
): RepositoryFunctionProcessor(logger) {

    override suspend fun process(
        declaration: KSFunctionDeclaration,
        repositoryName: String,
        packageDestination: String,
        serviceReturnType: KSType
    ): Set<GeneratedFileData> {
        return buildSet {

        }
    }


}