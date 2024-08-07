package org.mattshoe.shoebox.kernl

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

object DefaultKernlPolicy: KernlPolicy by KernlPolicyDefaults.copy()