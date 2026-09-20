#!/usr/bin/env bash
#
# Verifies that this API can be compiled on its own, without the CommonCapabilities mod
# and without loader-specific CyclopsCore classes.
#
# CyclopsCore consumes this repo as a submodule in a dedicated source set that only sees
# Minecraft, NeoForge and CyclopsCore's loader-common. Anything outside of that breaks its
# build, which is what this check guards against.
#
# Usage: .github/check-self-contained.sh [path-to-cyclopscore-checkout]

set -euo pipefail

API_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CYCLOPSCORE_DIR="${1:-}"
CYCLOPSCORE_COMMON=""

if [[ -n "${CYCLOPSCORE_DIR}" ]]; then
    CYCLOPSCORE_COMMON="${CYCLOPSCORE_DIR}/loader-common/src/main/java"
    if [[ ! -d "${CYCLOPSCORE_COMMON}" ]]; then
        echo "error: ${CYCLOPSCORE_COMMON} does not exist" >&2
        exit 1
    fi
fi

failures=0

fail() {
    echo "  $1" >&2
    failures=$((failures + 1))
}

# Reduce a dotted name to its top-level class, e.g. a.b.C.D.FIELD -> a.b.C
top_level_class() {
    local name="$1" out="" segment
    local IFS='.'
    for segment in ${name}; do
        out="${out:+${out}.}${segment}"
        if [[ "${segment}" =~ ^[A-Z] ]]; then
            echo "${out}"
            return
        fi
    done
    echo ""
}

# Collect every org.cyclops.* reference, both imports and inline fully-qualified usages.
# Only this repo's own tracked sources are scanned, so a CyclopsCore checkout that happens to
# sit inside the working directory is not picked up.
sources="$(git -C "${API_DIR}" ls-files '*.java')"
if [[ -z "${sources}" ]]; then
    echo "error: no java sources found in ${API_DIR}" >&2
    exit 1
fi
references="$(echo "${sources}" \
    | sed -E "s|^|${API_DIR}/|" \
    | xargs grep -hoE 'org\.cyclops\.[A-Za-z0-9_.]+' \
    | sed -E 's/\.$//' \
    | sort -u)"

while IFS= read -r reference; do
    [[ -n "${reference}" ]] || continue
    class="$(top_level_class "${reference}")"

    if [[ "${reference}" == org.cyclops.commoncapabilities.* ]]; then
        if [[ "${reference}" != org.cyclops.commoncapabilities.api.* ]]; then
            fail "${reference} lives in the CommonCapabilities mod, not in this API"
        fi
        continue
    fi

    if [[ "${reference}" == org.cyclops.cyclopscore.* ]]; then
        if [[ -z "${class}" ]]; then
            # A wildcard import or a bare package reference, nothing to resolve
            continue
        fi
        if [[ -z "${CYCLOPSCORE_COMMON}" ]]; then
            echo "  skipping ${class}, no CyclopsCore checkout given" >&2
            continue
        fi
        if [[ ! -f "${CYCLOPSCORE_COMMON}/${class//.//}.java" ]]; then
            fail "${class} is not in CyclopsCore's loader-common, so it is loader-specific"
        fi
        continue
    fi

    fail "${reference} is not part of this API, CyclopsCore's loader-common, Minecraft or NeoForge"
done <<< "${references}"

if [[ "${failures}" -gt 0 ]]; then
    echo "" >&2
    echo "${failures} disallowed reference(s) found, this API is no longer self-contained." >&2
    exit 1
fi

echo "This API is self-contained."
