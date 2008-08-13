<?xml version="1.0"?>
<algebra>
  <basicAlgebra>
    <algName>UnaryTerms(D16)</algName>
    <desc>The dihedral group of order 16</desc>
    <cardinality>16</cardinality>
    <universe>
      <elem>x</elem>
      <elem>s(x)</elem>
      <elem>t(x)</elem>
      <elem>s(s(x))</elem>
      <elem>s(t(x))</elem>
      <elem>t(s(x))</elem>
      <elem>s(s(s(x)))</elem>
      <elem>s(s(t(x)))</elem>
      <elem>t(s(s(x)))</elem>
      <elem>t(s(t(x)))</elem>
      <elem>s(s(s(s(x))))</elem>
      <elem>s(s(s(t(x))))</elem>
      <elem>t(s(s(s(x))))</elem>
      <elem>t(s(s(t(x))))</elem>
      <elem>s(s(s(s(s(x)))))</elem>
      <elem>s(s(s(s(t(x)))))</elem>
    </universe>
    <operations>
      <op>
        <opSymbol>
          <opName>prod</opName>
          <arity>2</arity>
        </opSymbol>
        <opTable>
          <intArray>
            <row r="[0]">0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15</row>
            <row r="[1]">1,3,4,6,7,2,10,11,5,0,14,15,8,9,13,12</row>
            <row r="[2]">2,5,0,8,9,1,12,13,3,4,15,14,6,7,11,10</row>
            <row r="[3]">3,6,7,10,11,4,14,15,2,1,13,12,5,0,9,8</row>
            <row r="[4]">4,2,1,5,0,3,8,9,6,7,12,13,10,11,15,14</row>
            <row r="[5]">5,8,9,12,13,0,15,14,1,2,11,10,3,4,7,6</row>
            <row r="[6]">6,10,11,14,15,7,13,12,4,3,9,8,2,1,0,5</row>
            <row r="[7]">7,4,3,2,1,6,5,0,10,11,8,9,14,15,12,13</row>
            <row r="[8]">8,12,13,15,14,9,11,10,0,5,7,6,1,2,4,3</row>
            <row r="[9]">9,0,5,1,2,8,3,4,12,13,6,7,15,14,10,11</row>
            <row r="[10]">10,14,15,13,12,11,9,8,7,6,0,5,4,3,1,2</row>
            <row r="[11]">11,7,6,4,3,10,2,1,14,15,5,0,13,12,8,9</row>
            <row r="[12]">12,15,14,11,10,13,7,6,9,8,4,3,0,5,2,1</row>
            <row r="[13]">13,9,8,0,5,12,1,2,15,14,3,4,11,10,6,7</row>
            <row r="[14]">14,13,12,9,8,15,0,5,11,10,1,2,7,6,3,4</row>
            <row r="[15]">15,11,10,7,6,14,4,3,13,12,2,1,9,8,5,0</row>
          </intArray>
        </opTable>
      </op>
    </operations>
  </basicAlgebra>
</algebra>
