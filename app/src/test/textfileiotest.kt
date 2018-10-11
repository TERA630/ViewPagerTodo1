import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowApplication


@RunWith(JUnit4::class)
class SerializersTest : Throwable() {

    @Test
    fun intSerializationTest() {
        // List <Int>
        val mockedContext = ShadowApplication.getInstance().applicationContext
        val testIntList = listOf(0, 1, 2, 3, 4)


        assertEquals(testIntList, deSerializedIntList)
    }
    @Test
    fun stringSerializationTest() {
        // List <String>
        val testStringList = listOf("dog", "cat", "bird", "fox")
        val s = StringSerializer
        val ls = ArrayListSerializer(s)
        val serializedStringList = JSON.unquoted.stringify(ls, testStringList)
        assertEquals("[dog,cat,bird,fox]", serializedStringList)
        val deserializedStringList = JSON.unquoted.parse(ls, serializedStringList)
        assertEquals(testStringList, deserializedStringList)
    }

}

