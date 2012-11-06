import sbt._
import sbt.Keys._

/**
 * Based on:
 * - https://github.com/T8Webware/sbt-license-plugin
 * - https://github.com/mrico/sbt-license-plugin
 */
object Licenses extends Plugin {

    import LicenseKeys._

    object LicenseKeys {
        lazy val formatLicenseHeaders = TaskKey[Unit]("format-license-headers", "Includes the license header to source files")
        lazy val license = SettingKey[String]("license", "The license text to use")
        lazy val removeExistingHeaderBlock = SettingKey[Boolean]("removeExistingHeaderBlock", "Removes existing header blocks")
    }

    def licenseSettings = Seq(
        license := apache2("Copyright (C) 2012 - 101loops.com <dev@101loops.com>"),
        removeExistingHeaderBlock := false,
        formatLicenseHeaders <<= formatLicenseHeadersTask,
        compileInputs in Compile <<= compileInputs in Compile dependsOn formatLicenseHeaders
    )


    private val lineSeparator = System.getProperty("line.separator")

    private def addHeader(path: File, fileContents: String, header: String,
                          removeExistingHeader: Boolean, log: Logger) {

        val withHeader = new File(path + ".withHeader")
        log.info("Adding license header to source file: " + path)

        IO.append(withHeader, header)
        IO.append(withHeader, lineSeparator)
        IO.append(withHeader,
            if (removeExistingHeader)
                withoutExistingHeaderBlock(fileContents)
            else
                fileContents)

        IO.copyFile(withHeader, path.asFile)

        if (!withHeader.delete)
            log.error("Unable to delete " + withHeader)
    }

    private def alreadyHasHeader(fileContents: String): Boolean = {
        val head = fileContents.take(200)
        head.startsWith("/*") &&
            (head.contains("License") || head.contains("Copyright"))
    }

    private def withoutExistingHeaderBlock(fileContents: String): String = {
        fileContents.split(lineSeparator).dropWhile {
            line => line.startsWith("/**") || line.startsWith(" *")
        } mkString (lineSeparator)
    }

    private def modifySources(sourceDirs: Seq[File], licenseText: String,
                              removeExistingHeaders: Boolean, log: Logger) {
        val header = licenseText
        sourceDirs.foreach {
            dir =>
                (dir ** ("*.scala" | "*.java")).get foreach {
                    path =>
                        val fileContents = IO.read(path)
                        if (!alreadyHasHeader(fileContents))
                            addHeader(path, fileContents, header, removeExistingHeaders, log)
                }
        }
    }

    private def formatLicenseHeadersTask =
        (streams, javaSource in Compile, javaSource in Test, scalaSource in Compile, scalaSource in Test,
            license in formatLicenseHeaders, removeExistingHeaderBlock in formatLicenseHeaders) map {
            (out, javaSrcDir, javaTestSrcDir, scalaSrcDir, scalaTestSrcDir, lic, removeHeader) =>
                val dirs = Seq(javaSrcDir, javaTestSrcDir, scalaSrcDir, scalaTestSrcDir)
                modifySources(dirs, lic, removeHeader, out.log)
        }

    private def apache2(copyright: String) =
        ("""/**
           | * """ + copyright + """
                                   | *
                                   | * Licensed under the Apache License, Version 2.0 (the "License");
                                   | * you may not use this file except in compliance with the License.
                                   | * You may obtain a copy of the License at
                                   | *
                                   | *         http://www.apache.org/licenses/LICENSE-2.0
                                   | *
                                   | * Unless required by applicable law or agreed to in writing, software
                                   | * distributed under the License is distributed on an "AS IS" BASIS,
                                   | * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                                   | * See the License for the specific language governing permissions and
                                   | * limitations under the License.
                                   | */""").stripMargin('|')

}