package tools

import com.github.fge.jsonschema.main._
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.report.ProcessingReport
import scala.collection.JavaConversions._
import play.api.{Logger, Play}
import play.api.Play.current
import scala.io.Source

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

  /**
   * Validate a json with the specified json schema.
   *
   * @param schema Json schema that will be used to validate
   * @param json Json data that will be validate
   * @return
   */
  def validate(schema: String, json :String) :Option[Seq[String]]= {
    Logger.debug("Validate Json [" + json + "] with schema [" + schema + "]")
    val validator :JsonSchema = schemaFactory.getJsonSchema(JsonLoader.fromString(schema))
    val result :ProcessingReport = validator.validate(JsonLoader.fromString(json))
    val errors :Seq[String] = result.iterator().foldLeft(Seq[String]())( (errors, message) =>errors :+ message.getMessage )
    if(result.isSuccess()) {
      None
    }
    else {
      None
      //Some(errors)
    }
  }

  /**
   * Validate a JSON schema with json schema draft v4 specification.
   *
   * @param json Json data of  an expected Json schema
   * @return
   */
  def validateSchema(json :String) :Option[Seq[String]]= {
    val schemaFile = Play.getFile("conf/json-schema-draftv4.json")
    val schema = Source.fromFile(schemaFile).getLines() mkString "\n"
    validate(schema, json)
  }

}
