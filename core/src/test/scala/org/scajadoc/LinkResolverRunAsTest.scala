package org.scajadoc

import org.specs.Specification
import org.specs.mock.Mockito
import org.junit.runner.RunWith
import org.specs.runner.{JUnitSuiteRunner, JUnit4}
import collection.mutable.{ListBuffer}
import util.{TemplateGenerator, linkResolver}
import tools.nsc.doc.model.{NonTemplateMemberEntity, DocTemplateEntity}

/**
 * Unit tests for utility class resolving links within internal and external api.
 *
 * @author Filip Rogaczewski
 */
@RunWith(classOf[JUnitSuiteRunner])
class LinkResolverRunAsTest extends JUnit4(linkResolverTest)

object linkResolverTest extends Specification("Specification of link resolver") with Mockito {

   var generator : TemplateGenerator = _

   private val path = "src/test/resources"

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
         linkedTemplate.inSource returns None
         linkedTemplate.isPackage returns false
         linkedTemplate.isClass returns true
         linkedTemplate.name returns "String"
         linkedTemplate.rawName returns "String"
         linkedTemplate.toRoot returns {
            val lang = mock[DocTemplateEntity]
            lang.name returns "lang"
            lang.rawName returns "lang"
            lang.isPackage returns true
            val java = mock[DocTemplateEntity]
            java.name returns "java"
            java.rawName returns "java"
            java.isPackage returns true
            List(linkedTemplate, lang, java)
         }
         linkResolver.resolve(linkedTemplate).get.link(null) mustEqual "http://download.oracle.com/javase/6/docs/api/java/lang/String.html"
      }
   }

   "Internal api resolution" in {
      doBefore(generator = new TemplateGenerator)
      "Resolve links to non templates entities" in {
         val member = generator.generate(path, "simpleMember")(0)
         linkResolver.resolve(member.asInstanceOf[NonTemplateMemberEntity]).get.link(member.inTemplate) mustEqual "../../org/scajadoc/Members.html#simpleMember"
      }
      "Resolve links to templates entities" in {
         val member = generator.generate(path, "Members")(0)
         linkResolver.resolve(member.asInstanceOf[DocTemplateEntity]).get.link(member.inTemplate) mustEqual "../../org/scajadoc/Members.html"
      }
      "Resolve links to templates entities from the higher node" in {
         val member = generator.generate(path, "Members")(0)
         val root = member.inTemplate.inTemplate
         linkResolver.resolve(member.asInstanceOf[DocTemplateEntity]).get.link(root) mustEqual "../org/scajadoc/Members.html"
      }
      "Resolve links to templates entities from the project root" in {
         val member = generator.generate(path, "Members")(0)
         val root = member.inTemplate.inTemplate.inTemplate
         linkResolver.resolve(member.asInstanceOf[DocTemplateEntity]).get.link(root) mustEqual "org/scajadoc/Members.html"
      }
   }

}