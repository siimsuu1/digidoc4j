/* DigiDoc4J library
 *
 * This software is released under either the GNU Library General Public
 * License (see LICENSE.LGPL).
 *
 * Note that the only valid version of the LGPL license as far as this
 * project is concerned is the original GNU Library General Public License
 * Version 2.1, February 1999
 */

package org.digidoc4j;

import org.digidoc4j.test.TestAssert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryInteroperabilityTest extends AbstractTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(LibraryInteroperabilityTest.class);

  @Test
  public void verifySignatureWithDigiDoc4j_BC_unsafe_integer_by_default() {
    Configuration configuration = Configuration.of(Configuration.Mode.PROD);
    String valueFromJvm = System.getProperty(Constant.System.ORG_BOUNCYCASTLE_ASN1_ALLOW_UNSAFE_INTEGER);
    LOGGER.info("JVM_BC_UNSAFE:" + valueFromJvm);
    LOGGER.info("CONF_BC_UNSAFE:" + configuration.isASN1UnsafeIntegerAllowed());

    Container container = ContainerBuilder.aContainer().
            fromExistingFile("src/test/resources/prodFiles/valid-containers/InvestorToomas.bdoc").
            withConfiguration(configuration).build();
    TestAssert.assertContainerIsValid(container);
  }
}
