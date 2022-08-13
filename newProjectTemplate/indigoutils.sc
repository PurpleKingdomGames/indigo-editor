import java.util.Properties

def readProp(name: String): String = {
  val props: Properties = new Properties
  props.load(os.read.inputStream(os.pwd / "settings.properties"))
  props.getProperty(name)
}

def title: String = readProp("title")
def width: Int = readProp("width").toInt
def height: Int = readProp("height").toInt
