package io.github.mattshoe.shoebox.autorepo.processors.impl

import util.RepositoryProcessorTestHarness

class NoCacheProcessorTest: RepositoryProcessorTestHarness() {

    override val annotationText = "@AutoRepo.NoCache"

}