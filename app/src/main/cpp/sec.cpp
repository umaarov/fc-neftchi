#include <jni.h>
#include <sys/ptrace.h>
#include <unistd.h>

extern "C" JNIEXPORT jboolean
Java_uz_umarov_fcneftchi_util_NativeSecurity_isDebuggerAttached(
        JNIEnv *env,
        jobject /* this */) {
    if (ptrace(PTRACE_TRACEME, 0, 1, 0) == -1) {
        return JNI_TRUE;
    } else {
        ptrace(PTRACE_DETACH, 0, 1, 0);
        return JNI_FALSE;
    }
}