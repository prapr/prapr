package com.esotericsoftware.spine;

@FunctionalInterface
public interface BoneProvider {
    public Bone newBone(BoneData boneData, Skeleton skeleton, Bone parent);
}
