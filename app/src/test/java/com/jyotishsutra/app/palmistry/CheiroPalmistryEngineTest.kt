package com.jyotishsutra.app.palmistry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheiroPalmistryEngineTest {
    private val engine = CheiroPalmistryEngine()

    @Test
    fun sameObservationProducesSameReading() {
        val observation = CheiroPalmistryEngine.sampleObservation()
        assertEquals(engine.interpret(observation), engine.interpret(observation))
    }

    @Test
    fun everySupportedLineAndMountHasAuditableEvidence() {
        val reading = engine.interpret(CheiroPalmistryEngine.sampleObservation())
        assertEquals(PalmLine.entries.toSet(), reading.lineInsights.keys)
        assertEquals(PalmMount.entries.toSet(), reading.mountInsights.keys)
        assertTrue(reading.lineInsights.values.all { it.evidence.isNotEmpty() && it.confidence in 0..100 })
        assertTrue(reading.mountInsights.values.all { it.evidence.isNotEmpty() && it.confidence in 0..100 })
    }

    @Test
    fun lifeLineNeverClaimsDeathOrLifespan() {
        val text = engine.interpret(CheiroPalmistryEngine.sampleObservation())
            .lineInsights.getValue(PalmLine.LIFE).summary.lowercase()
        assertFalse("death" in text)
        assertTrue("does not measure lifespan" in text)
    }

    @Test
    fun dominantAndNonDominantRolesRemainExplicit() {
        val active = engine.interpret(CheiroPalmistryEngine.sampleObservation(role = HandRole.DOMINANT))
        val inherited = engine.interpret(CheiroPalmistryEngine.sampleObservation(role = HandRole.NON_DOMINANT))
        assertEquals("Active tendencies", active.observation.role.displayName)
        assertEquals("Inherited tendencies", inherited.observation.role.displayName)
    }
}
