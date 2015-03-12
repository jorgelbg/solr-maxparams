package cu.uci.solr;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.CommonParams;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MaxParamsSearchComponentTest extends SolrTestCaseJ4 {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @BeforeClass
  public static void beforeClass() throws Exception {
    initCore("solrconfig-maxparams.xml", "schema.xml");
  }

  @Test
  public void testRequestWithinBoundaries() throws Exception {
    assertU(adoc("id", "4505", "subject", "Hoss the Hoss man Hostetter"));
    assertU(commit());
    assertU(optimize());

    assertQ("couldn't find subject hoss",
        req(CommonParams.Q, "*:*", CommonParams.ROWS, Integer.toString(4)),
        "//result[@numFound=1]",
        "//int[@name='id'][.='4505']");
  }

  @Test
  public void testRequestWithRowsParamExceeded() throws Exception {
    exception.expect(SolrException.class);
    exception.expectMessage(
        "Your start or rows parameter has exceeded the allowed values");

    h.query(
        req(CommonParams.Q, "*:*", CommonParams.ROWS, Integer.toString(400)));
  }

  @Test
  public void testRequestWithStartParamExceeded() throws Exception {
    exception.expect(SolrException.class);
    exception.expectMessage(
        "Your start or rows parameter has exceeded the allowed values");

    h.query(
        req(CommonParams.Q, "*:*", CommonParams.START, Integer.toString(5)));
  }

  @Test
  public void testOverwriteParams() throws Exception {
    assertU(adoc("id", "4505", "subject", "Hoss the Hoss man Hostetter"));
    assertU(commit());
    assertU(optimize());

    System.out.println(h.query(req("q", "*:*", "rows", Integer.toString(400),
        CommonParams.QT, "/overwrite")));

    assertQ("the parameters haven't been overwritten",
        req(CommonParams.Q, "*:*", CommonParams.ROWS, Integer.toString(400),
            CommonParams.QT, "/overwrite"),
        "//result[@numFound=1]",
        "//int[@name='id'][.='4505']");
  }
}
