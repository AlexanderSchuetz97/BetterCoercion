//
// Copyright Alexander Schütz, 2022
//
// This file is part of BetterCoercion.
//
// BetterCoercion is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// BetterCoercion is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// A copy of the GNU Lesser General Public License should be provided
// in the COPYING & COPYING.LESSER files in top level directory of BetterCoercion.
// If not, see <https://www.gnu.org/licenses/>.
//
package io.github.alexanderschuetz97.bettercoercion.userdata;

import org.luaj.vm2.Buffer;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

class LuaNumberUserdataView extends LuaNumber {

    private final LuaValue delegate;
    public LuaNumberUserdataView(AbstractInstance delegate) {
        this.delegate = delegate;
    }

    @Override
    public String tojstring() {
        return "number->" + delegate.tojstring();
    }

    //TABLE Stuff
    @Override
    public LuaTable checktable() {
        return delegate.checktable();
    }

    @Override
    public LuaTable opttable(LuaTable defval) {
        return delegate.opttable(defval);
    }

    @Override
    public LuaValue setmetatable(LuaValue metatable) {
        return delegate.setmetatable(metatable);
    }

    @Override
    public LuaValue getmetatable() {
        return delegate.getmetatable();
    }

    @Override
    public LuaValue get(int key) {
        return delegate.get(key);
    }

    @Override
    public LuaValue get(LuaValue key) {
        return delegate.get(key);
    }

    @Override
    public LuaValue rawget(int key) {
        return delegate.rawget(key);
    }

    @Override
    public LuaValue rawget(LuaValue key) {
        return delegate.rawget(key);
    }

    @Override
    public void set(int key, LuaValue value) {
        delegate.set(key, value);
    }

    @Override
    public void set(LuaValue key, LuaValue value) {
        delegate.set(key, value);
    }

    @Override
    public void rawset(int key, LuaValue value) {
        delegate.rawset(key, value);
    }

    @Override
    public void rawset(LuaValue key, LuaValue value) {
        delegate.rawset(key, value);
    }

    @Override
    public int length() {
        return delegate.length();
    }

    @Override
    public LuaValue len() {
        return delegate.len();
    }

    @Override
    public int rawlen() {
        return delegate.rawlen();
    }

    @Override
    public Varargs next(LuaValue key) {
        return delegate.next(key);
    }

    @Override
    public Varargs inext(LuaValue key) {
        return delegate.inext(key);
    }

    //USERDATA STUFF

    @Override
    public Object checkuserdata() {
        return delegate.checkuserdata();
    }

    @Override
    public Object checkuserdata(Class c) {
        return delegate.checkuserdata(c);
    }

    @Override
    public boolean isuserdata() {
        return delegate.isuserdata();
    }

    @Override
    public boolean isuserdata(Class c) {
        return delegate.isuserdata(c);
    }

    @Override
    public Object optuserdata(Object defval) {
        return delegate.optuserdata(defval);
    }

    @Override
    public Object optuserdata(Class c, Object defval) {
        return delegate.optuserdata(c, defval);
    }


    //NUMBER STUFF

    @Override
    public LuaString strvalue() {
        return delegate.strvalue();
    }

    @Override
    public LuaNumber checknumber() {
        return delegate.checknumber();
    }

    @Override
    public LuaNumber checknumber(String errmsg) {
        return delegate.checknumber(errmsg);
    }

    @Override
    public LuaNumber optnumber(LuaNumber defval) {
        return delegate.optnumber(defval);
    }

    @Override
    public LuaValue tonumber() {
        return delegate.tonumber();
    }

    @Override
    public LuaValue lt(LuaValue rhs) {
        return delegate.lt(rhs);
    }

    @Override
    public LuaValue lt(double rhs) {
        return delegate.lt(rhs);
    }

    @Override
    public LuaValue lt(int rhs) {
        return delegate.lt(rhs);
    }

    @Override
    public boolean lt_b(LuaValue rhs) {
        return delegate.lt_b(rhs);
    }

    @Override
    public boolean lt_b(int rhs) {
        return delegate.lt_b(rhs);
    }

    @Override
    public boolean lt_b(double rhs) {
        return delegate.lt_b(rhs);
    }

    @Override
    public LuaValue lteq(LuaValue rhs) {
        return delegate.lteq(rhs);
    }

    @Override
    public LuaValue lteq(double rhs) {
        return delegate.lteq(rhs);
    }

    @Override
    public LuaValue lteq(int rhs) {
        return delegate.lteq(rhs);
    }

    @Override
    public boolean lteq_b(LuaValue rhs) {
        return delegate.lteq_b(rhs);
    }

    @Override
    public boolean lteq_b(int rhs) {
        return delegate.lteq_b(rhs);
    }

    @Override
    public boolean lteq_b(double rhs) {
        return delegate.lteq_b(rhs);
    }

    @Override
    public LuaValue gt(LuaValue rhs) {
        return delegate.gt(rhs);
    }

    @Override
    public LuaValue gt(double rhs) {
        return delegate.gt(rhs);
    }

    @Override
    public LuaValue gt(int rhs) {
        return delegate.gt(rhs);
    }

    @Override
    public boolean gt_b(LuaValue rhs) {
        return delegate.gt_b(rhs);
    }

    @Override
    public boolean gt_b(int rhs) {
        return delegate.gt_b(rhs);
    }

    @Override
    public boolean gt_b(double rhs) {
        return delegate.gt_b(rhs);
    }

    @Override
    public LuaValue gteq(LuaValue rhs) {
        return delegate.gteq(rhs);
    }

    @Override
    public LuaValue gteq(double rhs) {
        return delegate.gteq(rhs);
    }

    @Override
    public LuaValue gteq(int rhs) {
        return delegate.gteq(rhs);
    }

    @Override
    public boolean gteq_b(LuaValue rhs) {
        return delegate.gteq_b(rhs);
    }

    @Override
    public boolean gteq_b(int rhs) {
        return delegate.gteq_b(rhs);
    }

    @Override
    public boolean gteq_b(double rhs) {
        return delegate.gteq_b(rhs);
    }

    @Override
    public boolean eq_b(LuaValue val) {
        return val == this || val == delegate;
    }

    @Override
    public boolean isint() {
        return delegate.isint();
    }

    @Override
    public boolean isinttype() {
        return delegate.isinttype();
    }

    @Override
    public boolean islong() {
        return delegate.islong();
    }

    @Override
    public boolean isnumber() {
        return delegate.isnumber();
    }

    @Override
    public boolean isstring() {
        return delegate.isstring();
    }

    @Override
    public boolean toboolean() {
        return delegate.toboolean();
    }

    @Override
    public byte tobyte() {
        return delegate.tobyte();
    }

    @Override
    public char tochar() {
        return delegate.tochar();
    }

    @Override
    public double todouble() {
        return delegate.todouble();
    }

    @Override
    public float tofloat() {
        return delegate.tofloat();
    }

    @Override
    public int toint() {
        return delegate.toint();
    }

    @Override
    public long tolong() {
        return delegate.tolong();
    }

    @Override
    public short toshort() {
        return delegate.toshort();
    }

    @Override
    public double optdouble(double defval) {
        return delegate.optdouble(defval);
    }

    @Override
    public int optint(int defval) {
        return delegate.optint(defval);
    }

    @Override
    public LuaInteger optinteger(LuaInteger defval) {
        return delegate.optinteger(defval);
    }

    @Override
    public long optlong(long defval) {
        return delegate.optlong(defval);
    }

    @Override
    public String optjstring(String defval) {
        return delegate.optjstring(defval);
    }

    @Override
    public LuaString optstring(LuaString defval) {
        return delegate.optstring(defval);
    }

    @Override
    public LuaValue optvalue(LuaValue defval) {
        return delegate.optvalue(defval);
    }

    @Override
    public double checkdouble() {
        return delegate.checkdouble();
    }

    @Override
    public int checkint() {
        return delegate.checkint();
    }

    @Override
    public LuaInteger checkinteger() {
        return delegate.checkinteger();
    }

    @Override
    public long checklong() {
        return delegate.checklong();
    }

    @Override
    public String checkjstring() {
        return delegate.checkjstring();
    }

    @Override
    public LuaString checkstring() {
        return delegate.checkstring();
    }

    @Override
    public LuaValue not() {
        return delegate.not();
    }

    @Override
    public LuaValue neg() {
        return delegate.neg();
    }

    @Override
    public LuaValue eq(LuaValue val) {
        return delegate.eq(val);
    }

    @Override
    public LuaValue neq(LuaValue val) {
        return delegate.neq(val);
    }

    @Override
    public boolean neq_b(LuaValue val) {
        return delegate.neq_b(val);
    }

    @Override
    public boolean raweq(LuaValue val) {
        return delegate.raweq(val);
    }

    @Override
    public boolean raweq(LuaUserdata val) {
        return delegate.raweq(val);
    }

    @Override
    public boolean raweq(LuaString val) {
        return delegate.raweq(val);
    }

    @Override
    public boolean raweq(double val) {
        return delegate.raweq(val);
    }

    @Override
    public boolean raweq(int val) {
        return delegate.raweq(val);
    }

    @Override
    public LuaValue add(LuaValue rhs) {
        return delegate.add(rhs);
    }

    @Override
    public LuaValue add(double rhs) {
        return delegate.add(rhs);
    }

    @Override
    public LuaValue add(int rhs) {
        return delegate.add(rhs);
    }

    @Override
    public LuaValue sub(LuaValue rhs) {
        return delegate.sub(rhs);
    }

    @Override
    public LuaValue sub(double rhs) {
        return delegate.sub(rhs);
    }

    @Override
    public LuaValue sub(int rhs) {
        return delegate.sub(rhs);
    }

    @Override
    public LuaValue subFrom(double lhs) {
        return delegate.subFrom(lhs);
    }

    @Override
    public LuaValue subFrom(int lhs) {
        return delegate.subFrom(lhs);
    }

    @Override
    public LuaValue mul(LuaValue rhs) {
        return delegate.mul(rhs);
    }

    @Override
    public LuaValue mul(double rhs) {
        return delegate.mul(rhs);
    }

    @Override
    public LuaValue mul(int rhs) {
        return delegate.mul(rhs);
    }

    @Override
    public LuaValue pow(LuaValue rhs) {
        return delegate.pow(rhs);
    }

    @Override
    public LuaValue pow(double rhs) {
        return delegate.pow(rhs);
    }

    @Override
    public LuaValue pow(int rhs) {
        return delegate.pow(rhs);
    }

    @Override
    public LuaValue powWith(double lhs) {
        return delegate.powWith(lhs);
    }

    @Override
    public LuaValue powWith(int lhs) {
        return delegate.powWith(lhs);
    }

    @Override
    public LuaValue div(LuaValue rhs) {
        return delegate.div(rhs);
    }

    @Override
    public LuaValue div(double rhs) {
        return delegate.div(rhs);
    }

    @Override
    public LuaValue div(int rhs) {
        return delegate.div(rhs);
    }

    @Override
    public LuaValue divInto(double lhs) {
        return delegate.divInto(lhs);
    }

    @Override
    public LuaValue mod(LuaValue rhs) {
        return delegate.mod(rhs);
    }

    @Override
    public LuaValue mod(double rhs) {
        return delegate.mod(rhs);
    }

    @Override
    public LuaValue mod(int rhs) {
        return delegate.mod(rhs);
    }

    @Override
    public LuaValue modFrom(double lhs) {
        return delegate.modFrom(lhs);
    }

    @Override
    public LuaValue concat(LuaValue rhs) {
        return delegate.concat(rhs);
    }

    @Override
    public LuaValue concatTo(LuaValue lhs) {
        return delegate.concatTo(lhs);
    }

    @Override
    public LuaValue concatTo(LuaNumber lhs) {
        return delegate.concatTo(lhs);
    }

    @Override
    public LuaValue concatTo(LuaString lhs) {
        return delegate.concatTo(lhs);
    }

    @Override
    public Buffer buffer() {
        return delegate.buffer();
    }

    @Override
    public Buffer concat(Buffer rhs) {
        return delegate.concat(rhs);
    }

    @Override
    public LuaValue and(LuaValue rhs) {
        return delegate.and(rhs);
    }

    @Override
    public LuaValue or(LuaValue rhs) {
        return delegate.or(rhs);
    }

    @Override
    public boolean testfor_b(LuaValue limit, LuaValue step) {
        return delegate.testfor_b(limit, step);
    }

    @Override
    public LuaValue strongvalue() {
        return delegate.strongvalue();
    }
}
