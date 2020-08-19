/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.spi.function;

import com.facebook.presto.common.function.SqlFunctionProperties;

import java.lang.invoke.MethodHandle;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class InProcessScalarFunctionImplementation
        implements ScalarFunctionImplementation
{
    private final boolean nullable;
    private final List<ArgumentProperty> argumentProperties;
    private final ReturnPlaceConvention returnPlaceConvention;
    private final MethodHandle methodHandle;
    private final Optional<MethodHandle> instanceFactory;
    private final boolean hasProperties;

    public InProcessScalarFunctionImplementation(
            boolean nullable,
            List<ArgumentProperty> argumentProperties,
            ReturnPlaceConvention returnPlaceConvention,
            MethodHandle methodHandle,
            Optional<MethodHandle> instanceFactory)
    {
        this.nullable = nullable;
        this.argumentProperties = argumentProperties;
        this.returnPlaceConvention = requireNonNull(returnPlaceConvention, "returnPlaceConvention is null");
        this.methodHandle = requireNonNull(methodHandle, "methodHandle is null");
        this.instanceFactory = requireNonNull(instanceFactory, "instanceFactory is null");
        this.hasProperties = methodHandle.type().parameterList().contains(SqlFunctionProperties.class);
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public List<ArgumentProperty> getArgumentProperties()
    {
        return argumentProperties;
    }

    public ArgumentProperty getArgumentProperty(int argumentIndex)
    {
        return argumentProperties.get(argumentIndex);
    }

    public ReturnPlaceConvention getReturnPlaceConvention()
    {
        return returnPlaceConvention;
    }

    public MethodHandle getMethodHandle()
    {
        return methodHandle;
    }

    public Optional<MethodHandle> getInstanceFactory()
    {
        return instanceFactory;
    }

    public boolean hasProperties()
    {
        return hasProperties;
    }

    public static class ArgumentProperty
    {
        // TODO: Alternatively, we can store com.facebook.presto.spi.type.Type
        private final ArgumentType argumentType;
        private final Optional<NullConvention> nullConvention;
        private final Optional<Class> lambdaInterface;

        public static ArgumentProperty valueTypeArgumentProperty(NullConvention nullConvention)
        {
            return new ArgumentProperty(ArgumentType.VALUE_TYPE, Optional.of(nullConvention), Optional.empty());
        }

        public static ArgumentProperty functionTypeArgumentProperty(Class lambdaInterface)
        {
            return new ArgumentProperty(ArgumentType.FUNCTION_TYPE, Optional.empty(), Optional.of(lambdaInterface));
        }

        public ArgumentProperty(ArgumentType argumentType, Optional<NullConvention> nullConvention, Optional<Class> lambdaInterface)
        {
            this.argumentType = argumentType;
            this.nullConvention = nullConvention;
            this.lambdaInterface = lambdaInterface;
        }

        public ArgumentType getArgumentType()
        {
            return argumentType;
        }

        public Optional<NullConvention> getNullConvention()
        {
            return nullConvention;
        }

        public Optional<Class> getLambdaInterface()
        {
            return lambdaInterface;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            ArgumentProperty other = (ArgumentProperty) obj;
            return this.argumentType == other.argumentType &&
                this.nullConvention.equals(other.nullConvention) &&
                this.lambdaInterface.equals(other.lambdaInterface);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(nullConvention, lambdaInterface);
        }
    }

    public enum NullConvention
    {
        RETURN_NULL_ON_NULL(1),
        USE_BOXED_TYPE(1),
        USE_NULL_FLAG(2),
        BLOCK_AND_POSITION(2),
        /**/;

        private final int parameterCount;

        NullConvention(int parameterCount)
        {
            this.parameterCount = parameterCount;
        }

        public int getParameterCount()
        {
            return parameterCount;
        }
    }

    public enum ArgumentType
    {
        VALUE_TYPE,
        FUNCTION_TYPE
    }

    public enum ReturnPlaceConvention
    {
        STACK,
        PROVIDED_BLOCKBUILDER
    }
}
