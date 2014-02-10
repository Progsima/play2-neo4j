package tools

import com.github.fge.jsonschema.main._
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.report.ProcessingReport
import scala.collection.JavaConversions._

/**
 * Json tools that helps to validate json with schema.
 *
 * @author : bsimard
 */
object JsonTools {

  /**
   * The default Json schema validator
   */
  val schemaFactory: JsonSchemaFactory = JsonSchemaFactory.byDefault()

  def validate(schema: String, json :String) :Option[Seq[String]]= {
    val validator :JsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(schema))
    val result :ProcessingReport = validator.validate(JsonLoader.fromString(json))
    if(result.isSuccess) {
      None
    }
    else {
      val errors :Seq[String] = result.iterator().foldLeft(Seq[String]())( (errors, message) =>errors :+ message.getMessage )
      Some(errors)
    }
  }

}
