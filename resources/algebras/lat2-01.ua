<?xml version="1.0"?>
<algebra>
  <basicAlgebra>
    <algName>lat2-01</algName>
    <desc>The 2 element lattice with 0, 1 as constants.</desc>
    <cardinality>2</cardinality>
    <operations>
      <op>
        <opSymbol>
          <opName>join</opName>
          <arity>2</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row r="[0]">0,1</row>
            <row r="[1]">1,1</row>
          </intArray>
        </opTable>
      </op>
      <op>
        <opSymbol>
          <opName>meet</opName>
          <arity>2</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row r="[0]">0,0</row>
            <row r="[1]">0,1</row>
          </intArray>
        </opTable>
      </op>
      <op>
        <opSymbol>
          <opName>zero</opName>
          <arity>0</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row>0</row>
          </intArray>
        </opTable>
      </op>
      <op>
        <opSymbol>
          <opName>one</opName>
          <arity>0</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row>1</row>
          </intArray>
        </opTable>
      </op>
    </operations>
  </basicAlgebra>
</algebra>
