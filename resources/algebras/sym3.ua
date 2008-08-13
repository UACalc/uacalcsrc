<?xml version="1.0"?>
<algebra>
  <basicAlgebra>
    <algName>Sym3</algName>
    <desc>S_3. 0 = id, 1 = (123), 2 = (132), 3 = (12), 4 = (13), 5 = (23).</desc>
    <cardinality>6</cardinality>
    <operations>
      <op>
        <opSymbol>
          <opName>p</opName>
          <arity>2</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row r="[0]">0,1,2,3,4,5</row>
            <row r="[1]">1,2,0,4,5,3</row>
            <row r="[2]">2,0,1,5,3,4</row>
            <row r="[3]">3,5,4,0,2,1</row>
            <row r="[4]">4,3,5,1,0,2</row>
            <row r="[5]">5,4,3,2,1,0</row>
          </intArray>
        </opTable>
      </op>
    </operations>
  </basicAlgebra>
</algebra>
