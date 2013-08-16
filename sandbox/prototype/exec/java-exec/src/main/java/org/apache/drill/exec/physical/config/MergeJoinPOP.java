package org.apache.drill.exec.physical.config;

import java.util.Iterator;
import java.util.List;

import org.apache.drill.common.logical.data.JoinCondition;
import org.apache.drill.exec.physical.OperatorCost;
import org.apache.drill.exec.physical.base.AbstractBase;
import org.apache.drill.exec.physical.base.PhysicalOperator;
import org.apache.drill.exec.physical.base.PhysicalVisitor;
import org.apache.drill.exec.physical.base.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;

@JsonTypeName("merge-join")
public class MergeJoinPOP extends AbstractBase{
  static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MergeJoinPOP.class);

  
  private PhysicalOperator left;
  private PhysicalOperator right;
  private List<JoinCondition> conditions;
  
  @Override
  public OperatorCost getCost() {
    return new OperatorCost(0,0,0,0);
  }

  @JsonCreator
  public MergeJoinPOP(
      @JsonProperty("left") PhysicalOperator left, 
      @JsonProperty("right") PhysicalOperator right,
      @JsonProperty("join-conditions") List<JoinCondition> conditions 
      ) {
    this.left = left;
    this.right = right;
    this.conditions = conditions;
  }
  
  @Override
  public Size getSize() {
    return left.getSize().add(right.getSize());
  }

  @Override
  public <T, X, E extends Throwable> T accept(PhysicalVisitor<T, X, E> physicalVisitor, X value) throws E {
    return physicalVisitor.visitMergeJoin(this, value);
  }

  @Override
  public PhysicalOperator getNewWithChildren(List<PhysicalOperator> children) {
    Preconditions.checkArgument(children.size() == 2);
    return new MergeJoinPOP(children.get(0), children.get(1), conditions);
  }

  @Override
  public Iterator<PhysicalOperator> iterator() {
    return Iterators.forArray(left, right);
  }

  public List<JoinCondition> getConditions() {
    return conditions;
  }
}