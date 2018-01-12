# API Docs - v4.0.0-SNAPSHOT

## Store

### hazelcast *<a target="_blank" href="https://wso2.github.io/siddhi/documentation/siddhi-4.0/#store">(Store)</a>*

<p style="word-wrap: break-word"> </p>

<span id="syntax" class="md-typeset" style="display: block; font-weight: bold;">Syntax</span>
```
@Store(type="hazelcast", cluster.name="<STRING>", cluster.password="<STRING>", collection.name="<STRING>", cluster.addresses="<STRING>")
@PrimaryKey("PRIMARY_KEY")
@Index("INDEX")
```

<span id="query-parameters" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">QUERY PARAMETERS</span>
<table>
    <tr>
        <th>Name</th>
        <th style="min-width: 20em">Description</th>
        <th>Default Value</th>
        <th>Possible Data Types</th>
        <th>Optional</th>
        <th>Dynamic</th>
    </tr>
    <tr>
        <td style="vertical-align: top">cluster.name</td>
        <td style="vertical-align: top; word-wrap: break-word">Hazelcast cluster name</td>
        <td style="vertical-align: top">dev</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">cluster.password</td>
        <td style="vertical-align: top; word-wrap: break-word">Hazelcast cluster password </td>
        <td style="vertical-align: top">dev-pass</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">collection.name</td>
        <td style="vertical-align: top; word-wrap: break-word">Name of the collection </td>
        <td style="vertical-align: top">Name of the siddhi event table.</td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">Yes</td>
        <td style="vertical-align: top">No</td>
    </tr>
    <tr>
        <td style="vertical-align: top">cluster.addresses</td>
        <td style="vertical-align: top; word-wrap: break-word">Hazelcast cluster address </td>
        <td style="vertical-align: top"></td>
        <td style="vertical-align: top">STRING</td>
        <td style="vertical-align: top">No</td>
        <td style="vertical-align: top">No</td>
    </tr>
</table>

<span id="examples" class="md-typeset" style="display: block; font-weight: bold;">Examples</span>
<span id="example-1" class="md-typeset" style="display: block; color: rgba(0, 0, 0, 0.54); font-size: 12.8px; font-weight: bold;">EXAMPLE 1</span>
```
define stream StockStream (symbol string, price float, volume long); @Store(type="hazelcast", cluster.name='dev', cluster.password='dev-pass', cluster.addresses='172.17.0.1:5701',collection.name='collection-name')define table StockTable (symbol string, price float, volume long);
```
<p style="word-wrap: break-word">This definition creates an event table named <code>StockTable  on the Hazelcast instance The connection is made as specified by the parameters configured for the '@Store' annotation. The </code>symbol` attribute is considered a unique field</p>

