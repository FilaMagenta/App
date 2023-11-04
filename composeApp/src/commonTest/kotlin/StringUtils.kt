import com.arnyminerz.filamagenta.utils.isValidDni
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringUtils {
    @Test
    fun `test DNI validation`() {
        assertFalse("".isValidDni)
        assertFalse("1".isValidDni)
        assertFalse("a".isValidDni)
        assertFalse("12345678".isValidDni)
        assertFalse("123456789".isValidDni)
        assertFalse("abcdefghi".isValidDni)
        assertFalse("abcdefghij".isValidDni)

        assertTrue("12345678Z".isValidDni)
        assertTrue("12345678z".isValidDni)
    }
}
