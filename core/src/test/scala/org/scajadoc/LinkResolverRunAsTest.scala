package org.scajadoc

import org.specs.Specification
import org.specs.mock.Mockito
import tools.nsc.doc.model.DocTemplateEntity
import org.junit.runner.RunWith
import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import collection.mutable.{ListBuffer}

/**
 * Unit tests for utility class resolving links within internal and external api.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class LinkResolverRunAsTest extends JUnit4(linkResolverTest)

object linkResolverTest extends Specification("Specification of link resolver") with Mockito {

   "Link resolver" should {
      "Read list of packages from an online package-list file" in {
         val links = new ListBuffer[String]
         links += "http://download.oracle.com/javase/6/docs/api/"
         settings.links = links.toList
         linkResolver.getLinksToExternalDependencies.get("java.lang").get mustEqual "http://download.oracle.com/javase/6/docs/api/"
      }
      "Read list of packages from multiple online package-list files" in {
         val links = new ListBuffer[String]
         links += "http://download.oracle.com/javase/6/docs/api/"
         links += "http://download.oracle.com/javaee/6/api/"
         settings.links = links.toList
         linkResolver.getLinksToExternalDependencies.get("javax.inject").get mustEqual "http://download.oracle.com/javaee/6/api/"
      }
      "Not attemp to retrieve list of packages when no links are specified" in {
         settings.links = Nil:List[String]
         linkResolver.getLinksToExternalDependencies.get("javax.inject") mustBe None
      }
   }

   "External api resolution" should {
      "Resolve links to external class from java package" in {
         val links = new ListBuffer[String]
         links += "http://download.oracle.com/javase/6/docs/api/"
         settings.links = links.toList
         val linkedTemplate = mock[DocTemplateEntity]
         linkedTemplate.sourceUrl returns None
         linkedTemplate.isPackage returns false
         linkedTemplate.isClass returns true
         linkedTemplate.name returns "String"
         linkedTemplate.toRoot returns {
            val lang = mock[DocTemplateEntity]
            lang.name returns "lang"
            lang.isPackage returns true
            val java = mock[DocTemplateEntity]
            java.name returns "java"
            java.isPackage returns true
            List(linkedTemplate, lang, java)
         }
         linkResolver.resolve(linkedTemplate).get.absoluteLink mustEqual "http://download.oracle.com/javase/6/docs/api/java/lang/String.html"
      }
   }

}