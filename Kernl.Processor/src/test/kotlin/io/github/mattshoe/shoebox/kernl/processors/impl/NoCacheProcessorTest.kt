package io.github.mattshoe.shoebox.kernl.processors.impl

import util.RepositoryProcessorTestHarness

class NoCacheProcessorTest: RepositoryProcessorTestHarness() {

    override val annotationText = "@Kernl.NoCache"

}