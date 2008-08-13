<?xml version="1.0"?>
<algebra>
  <basicAlgebra>
    <algName>m4</algName>
    <desc>M_4 with 1 as the greatest element.</desc>
    <cardinality>6</cardinality>
    <operations>
      <op>
        <opSymbol>
          <opName>join</opName>
          <arity>2</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row r="[0]">0,1,2,3,4,5</row>
            <row r="[1]">1,1,1,1,1,1</row>
            <row r="[2]">2,1,2,1,1,1</row>
            <row r="[3]">3,1,1,3,1,1</row>
            <row r="[4]">4,1,1,1,4,1</row>
            <row r="[5]">5,1,1,1,1,5</row>
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
            <row r="[0]">0,0,0,0,0,0</row>
            <row r="[1]">0,1,2,3,4,5</row>
            <row r="[2]">0,2,2,0,0,0</row>
            <row r="[3]">0,3,0,3,0,0</row>
            <row r="[4]">0,4,0,0,4,0</row>
            <row r="[5]">0,5,0,0,0,5</row>
          </intArray>
        </opTable>
      </op>
    </operations>
  </basicAlgebra>
</algebra>
