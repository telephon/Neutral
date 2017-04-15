
// Extendable

a = Extendable.new;

a.pingolino = { |self, x, y, z| [x, y, z].postln; 999 };

a.pingolino(1, 2, 3) // 999

a.pongiloni = { |self, x, y, z| x + y - z };

a.pingolino = { |self, x, y, z| self.pongiloni(x, y, z) }; // forward

a.pingolino(10, 2, 3) // 9


a.totalFree = { 1.0.rand } // exists, should throw






// ExtendableObject

a = ExtendableObject(8);

a + 9; // 17

a.zeta = { |self, y = 1| self.object * y };

a.zeta // 8

a.zeta(10); // 80

a.totalFree = { 1.0.rand }; // exists, should throw

a.addMethod('+', { pi }); // override

a + 6 == pi // yes

a.addMethod('+', nil); // remove

a + 6 == 14 // yes







// Halo2

a = Halo2(2);

a + 4

4 + a // should this work?

a.addHalo(\data, { "yes" });

a.getHalo(\data);
