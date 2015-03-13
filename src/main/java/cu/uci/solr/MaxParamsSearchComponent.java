package cu.uci.solr;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.util.plugin.SolrCoreAware;

import java.io.IOException;

/**
 * Block requests with a higher start or rows parameters than the configured in this components, this could be used
 * as some safeguard policy for some use cases
 * <p/>
 * <pre class="prettyprint">
 * <p/>
 * &lt;searchComponent name="max-parameters" class="cu.uci.solr.MaxParamsSearchComponent"/&gt;
 * &lt;str name="rows"&gt;1000&lt;/str&gt;
 * &lt;str name="start"&gt;1000&lt;/str&gt;
 * &lt;str name="overwriteParams"&gt;false&lt;/str&gt;
 * &lt;/searchComponent&gt;</pre>
 *
 * @version 1.0
 */
public class MaxParamsSearchComponent extends SearchComponent
    implements SolrCoreAware {

  private int maxRowsParam;
  private int maxStartParam;
  private boolean overwriteParams;

  @Override
  public void init(NamedList args) {
    if (null != args) {
      SolrParams params = SolrParams.toSolrParams(args);

      maxRowsParam = params.getInt("rows", -1);
      maxStartParam = params.getInt("start", -1);

      if (maxRowsParam == -1 && maxStartParam == -1) {
        throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
            "You must set the rows or start parameters in the configuration of this component");
      }

      overwriteParams = params.getBool("overwriteParams", false);
    }
  }

  @Override
  public void inform(SolrCore core) {

  }

  @Override
  public void prepare(ResponseBuilder rb) throws IOException {
    SolrParams params = rb.req.getParams();

    int rows = params.getInt("rows", -1);
    int start = params.getInt("start", -1);

    if (rows == -1 && start == -1) {
      // nothing to do, there is no rows or start parameter
      return;
    } else {
      if (rows > maxRowsParam || start > maxStartParam) {
        if (overwriteParams) {
          // overwrite the rows and start parameters
          ModifiableSolrParams modifiableSolrParams = new ModifiableSolrParams(
              params);

          if (rows > maxRowsParam) {
            modifiableSolrParams.set("rows", maxRowsParam);
          }

          if (start > maxStartParam) {
            modifiableSolrParams.set("start", maxStartParam);
          }

          rb.req.setParams(modifiableSolrParams);
        } else {
          throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
              "Your start or rows parameter has exceeded the allowed values");
        }
      }
    }
  }

  @Override
  public void process(ResponseBuilder rb) throws IOException {

  }

  @Override
  public String getDescription() {
    return "A component to set max values for start and rows parameters";
  }

  @Override
  public String getSource() {
    return "MaxParamsSearchComponent.java";
  }
}

